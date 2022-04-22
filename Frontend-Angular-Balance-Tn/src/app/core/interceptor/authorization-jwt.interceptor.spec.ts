import { TestBed } from '@angular/core/testing';

import { AuthorizationJwtInterceptor } from './authorization-jwt.interceptor';

describe('AuthorizationJwtInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      AuthorizationJwtInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: AuthorizationJwtInterceptor = TestBed.inject(AuthorizationJwtInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
