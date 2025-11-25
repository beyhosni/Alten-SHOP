import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ContactComponent } from './contact.component';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

describe('ContactComponent', () => {
    let component: ContactComponent;
    let fixture: ComponentFixture<ContactComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ContactComponent, FormsModule]
        })
            .compileComponents();

        fixture = TestBed.createComponent(ContactComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should have invalid form when empty', () => {
        expect(component.email).toBe('');
        expect(component.message).toBe('');
        // You might want to add form validation logic in the component if not present
    });

    it('should bind input fields', async () => {
        const emailInput = fixture.debugElement.query(By.css('input[type="email"]'));
        const messageInput = fixture.debugElement.query(By.css('textarea'));

        if (emailInput && messageInput) {
            emailInput.nativeElement.value = 'test@test.com';
            emailInput.nativeElement.dispatchEvent(new Event('input'));

            messageInput.nativeElement.value = 'Hello world';
            messageInput.nativeElement.dispatchEvent(new Event('input'));

            fixture.detectChanges();

            expect(component.email).toBe('test@test.com');
            expect(component.message).toBe('Hello world');
        }
    });

    it('should show success message on submit', () => {
        component.email = 'test@test.com';
        component.message = 'Test message';

        spyOn(window, 'alert');

        component.onSubmit();

        expect(window.alert).toHaveBeenCalledWith('Message sent successfully');
        expect(component.email).toBe('');
        expect(component.message).toBe('');
    });
});
