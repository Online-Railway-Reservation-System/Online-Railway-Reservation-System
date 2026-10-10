import { Injectable, signal, computed, effect } from '@angular/core';
import { SupportedLanguage, TRANSLATIONS, Translations } from '../i18n/translations';

const STORAGE_KEY = 'pr_selected_lang';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  // Always default to English
  readonly currentLang = signal<SupportedLanguage>('en');

  readonly isUrdu = computed(() => this.currentLang() === 'ur');
  readonly direction = computed<'ltr' | 'rtl'>(() => this.currentLang() === 'ur' ? 'rtl' : 'ltr');

  constructor() {
    this.initLanguage();

    // Dynamically update document direction and language attributes
    effect(() => {
      const lang = this.currentLang();
      const dir = this.direction();

      if (typeof document !== 'undefined') {
        document.documentElement.setAttribute('lang', lang);
        document.documentElement.setAttribute('dir', dir);
        if (dir === 'rtl') {
          document.body.classList.add('rtl-mode');
        } else {
          document.body.classList.remove('rtl-mode');
        }
      }
    });
  }

  private initLanguage(): void {
    if (typeof localStorage !== 'undefined') {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved === 'ur') {
        this.currentLang.set('ur');
      } else {
        this.currentLang.set('en');
      }
    }
  }

  setLanguage(lang: SupportedLanguage): void {
    this.currentLang.set(lang);
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(STORAGE_KEY, lang);
    }
  }

  toggleLanguage(): void {
    const next = this.currentLang() === 'en' ? 'ur' : 'en';
    this.setLanguage(next);
  }

  translate(key: string): string {
    const lang = this.currentLang();
    const parts = key.split('.');
    let current: any = TRANSLATIONS[lang];

    for (const part of parts) {
      if (current && typeof current === 'object' && part in current) {
        current = current[part];
      } else {
        // Fallback to English if missing in Urdu
        let fallback: any = TRANSLATIONS['en'];
        for (const fbPart of parts) {
          if (fallback && typeof fallback === 'object' && fbPart in fallback) {
            fallback = fallback[fbPart];
          } else {
            return key; // return key as last resort
          }
        }
        return typeof fallback === 'string' ? fallback : key;
      }
    }

    return typeof current === 'string' ? current : key;
  }
}
