export interface ShoppingList {
  id: string;
  name: string;
  items: ShoppingItem[];
}

export interface ShoppingItem {
  id: string;
  name: string;
  quantity: number;
  purchased: boolean;
}
