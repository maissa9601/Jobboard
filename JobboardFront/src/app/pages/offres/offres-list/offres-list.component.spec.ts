import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OffresListComponent } from './offres-list.component';

describe('OffresListComponent', () => {
  let component: OffresListComponent;
  let fixture: ComponentFixture<OffresListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OffresListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OffresListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
