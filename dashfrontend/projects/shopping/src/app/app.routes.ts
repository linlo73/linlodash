import { Routes } from '@angular/router';
import { authGuard, LoginComponent, RegisterComponent } from '@linlo/auth';
import { ListsComponent } from './lists/lists.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'lists' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'lists', component: ListsComponent, canActivate: [authGuard] }
];
