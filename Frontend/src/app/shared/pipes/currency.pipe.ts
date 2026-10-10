import { Pipe, PipeTransform, inject } from '@angular/core';
import { LanguageService } from '../../core/services/language.service';

@Pipe({
  name: 'pkr',
  standalone: true,
  pure: false
})
export class PkrCurrencyPipe implements PipeTransform {
  private langService = inject(LanguageService);

  transform(value: number | string | null | undefined, prefix: boolean = true): string {
    if (value === null || value === undefined || value === '') return 'PKR 0';

    const num = typeof value === 'string' ? parseFloat(value) : value;
    if (isNaN(num)) return 'PKR 0';

    const formatted = new Intl.NumberFormat('en-PK', {
      maximumFractionDigits: 2,
      minimumFractionDigits: num % 1 !== 0 ? 2 : 0
    }).format(num);

    const isUrdu = this.langService.isUrdu();
    if (isUrdu) {
      return prefix ? `${formatted} روپے` : formatted;
    }

    return prefix ? `PKR ${formatted}` : formatted;
  }
}
