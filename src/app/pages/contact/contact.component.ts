import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../shared/header/header.component';
import { FooterComponent } from '../../shared/footer/footer.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, FooterComponent],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css'
})
export class ContactComponent {
  contactForm = {
    name: '',
    email: '',
    message: ''
  };

  submitForm(): void {
    if (!this.contactForm.name || !this.contactForm.email || !this.contactForm.message) {
      Swal.fire({
        title: 'Thiếu thông tin!',
        text: 'Vui lòng điền đầy đủ thông tin',
        icon: 'warning',
        confirmButtonColor: '#EE4D2D'
      });
      return;
    }

    // Giả lập gửi form
    Swal.fire({
      title: 'Gửi thành công!',
      text: 'Chúng tôi sẽ liên hệ với bạn sớm nhất',
      icon: 'success',
      confirmButtonColor: '#EE4D2D'
    });

    // Reset form
    this.contactForm = {
      name: '',
      email: '',
      message: ''
    };
  }
}
