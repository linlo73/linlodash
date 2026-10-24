import { Injectable, signal } from '@angular/core';
import { ShoppingItem, ShoppingList } from '../models/shopping.models';

@Injectable({ providedIn: 'root' })
export class ShoppingService {
  readonly lists = signal<ShoppingList[]>([
    { id: '1', name: 'Spesa settimanale', items: [
      { id: '1', name: 'Latte', quantity: 2, purchased: false },
      { id: '2', name: 'Pane', quantity: 1, purchased: false }
    ] }
  ]);

  addList(name: string): void {
    const trimmed = name.trim();
    if (!trimmed) return;
    this.lists.update(lists => [...lists, { id: crypto.randomUUID(), name: trimmed, items: [] }]);
  }

  addItem(listId: string, name: string, quantity = 1): void {
    const trimmed = name.trim();
    if (!trimmed) return;
    this.lists.update(lists => lists.map(list =>
      list.id === listId
        ? { ...list, items: [...list.items, { id: crypto.randomUUID(), name: trimmed, quantity, purchased: false }] }
        : list
    ));
  }

  toggleItem(listId: string, itemId: string): void {
    this.lists.update(lists => lists.map(list =>
      list.id === listId
        ? { ...list, items: list.items.map(item => item.id === itemId ? { ...item, purchased: !item.purchased } : item) }
        : list
    ));
  }
}
