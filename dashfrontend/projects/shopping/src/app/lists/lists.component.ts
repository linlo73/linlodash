import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ShoppingService } from '../services/shopping.service';

@Component({
  selector: 'shopping-lists',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './lists.component.html'
})
export class ListsComponent {
  readonly shopping = inject(ShoppingService);
  readonly newListName = signal('');

  addList(): void {
    this.shopping.addList(this.newListName());
    this.newListName.set('');
  }

  addItem(listId: string, input: HTMLInputElement): void {
    this.shopping.addItem(listId, input.value);
    input.value = '';
  }
}
