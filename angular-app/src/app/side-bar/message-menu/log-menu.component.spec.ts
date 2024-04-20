import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogMenuComponent } from './log-menu.component';

describe('MessageMenuComponent', () => {
  let component: LogMenuComponent;
  let fixture: ComponentFixture<LogMenuComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LogMenuComponent]
    });
    fixture = TestBed.createComponent(LogMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
