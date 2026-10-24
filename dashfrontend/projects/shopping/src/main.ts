import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { authInterceptor, provideAuth } from '@linlo/auth';

async function loadConfig(): Promise<{ apiUrl: string }> {
  const response = await fetch(new URL('config.json', document.baseURI));
  if (!response.ok) {
    throw new Error(`Caricamento di config.json fallito: HTTP ${response.status}`);
  }

  const config: unknown = await response.json();
  if (
    typeof config !== 'object' ||
    config === null ||
    !('apiUrl' in config) ||
    typeof config.apiUrl !== 'string' ||
    !config.apiUrl.trim()
  ) {
    throw new Error('config.json non contiene un apiUrl valido');
  }

  return { apiUrl: config.apiUrl.replace(/\/+$/, '') };
}

loadConfig()
  .then(({ apiUrl }) =>
    bootstrapApplication(AppComponent, {
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideAuth({ apiUrl }),
        provideRouter(routes)
      ]
    })
  )
  .catch(error => console.error('Avvio dell’app shopping fallito:', error));
