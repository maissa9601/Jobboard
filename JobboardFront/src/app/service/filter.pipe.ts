import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filterByField',
  standalone: true
})
export class FilterByFieldPipe implements PipeTransform {
  transform(items: any[], searchText: string, field: string): any[] {
    if (!items || !searchText || !field) return items;

    searchText = searchText.toLowerCase();
    return items.filter(item =>
      item[field]?.toLowerCase().includes(searchText)
    );
  }
}

