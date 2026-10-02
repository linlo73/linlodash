# Angular Best Practices for Qodo

Always generate Angular code following the latest modern standards:
- Use **Standalone Components** by default (do not use NgModules).
- Use **Angular Signals** (`signal()`, `computed()`, `effect()`) for state management instead of RxJS for simple local state.
- Use the **new control flow** syntax in HTML templates (`@if`, `@for`, `@switch`) instead of `*ngIf` or `*ngFor`.
- Ensure strict TypeScript typing.
