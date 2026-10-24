import { Routes } from '@angular/router';
import { LoginComponent, RegisterComponent } from '@linlo/auth';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent }
];
