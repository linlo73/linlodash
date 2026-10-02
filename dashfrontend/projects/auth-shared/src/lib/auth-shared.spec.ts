import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthShared } from './auth-shared';

describe('AuthShared', () => {
  let component: AuthShared;
  let fixture: ComponentFixture<AuthShared>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthShared]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AuthShared);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
