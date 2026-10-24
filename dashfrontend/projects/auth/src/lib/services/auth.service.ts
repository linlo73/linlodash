import { Injectable, InjectionToken, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthConfig, LoginRequest, RegisterRequest, User } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly config = inject<AuthConfig>(AUTH_CONFIG);
  readonly user = signal<User | null>(null);

  login(request: LoginRequest): Observable<User> {
    return this.http.post<User>(`${this.config.apiUrl}/auth/login`, request, { withCredentials: true })
      .pipe(tap(username => this.user.set(username)));
  }

  register(request: RegisterRequest): Observable<User> {
    return this.http.post<User>(`${this.config.apiUrl}/auth/register`, request, { withCredentials: true })
      .pipe(tap(username => this.user.set(username)));
  }

  me(): Observable<User> {
    return this.http.get<User>(`${this.config.apiUrl}/auth/me`, { withCredentials: true })
      .pipe(tap(username => this.user.set(username)));
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.config.apiUrl}/auth/logout`, {}, { withCredentials: true })
      .pipe(tap(() => this.user.set(null)));
  }

  isAuthenticated(): boolean {
    return this.user() !== null;
  }
}

export const AUTH_CONFIG = new InjectionToken<AuthConfig>('AUTH_CONFIG');

export function provideAuth(config: AuthConfig) {
  return { provide: AUTH_CONFIG, useValue: config };
}
