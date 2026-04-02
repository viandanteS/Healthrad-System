import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Appuntamenti } from './appuntamenti';

describe('Appuntamenti', () => {
  let component: Appuntamenti;
  let fixture: ComponentFixture<Appuntamenti>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Appuntamenti]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Appuntamenti);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
