import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../service/admin.service';
import { FormsModule } from '@angular/forms';
import { FilterPipe } from '../../../service/filter.pipe';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  imports: [CommonModule, FormsModule, FilterPipe],
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  activeTab = 'admins';

  admins: any[] = [];
  candidats: any[] = [];
  offres: any[] = [];

  newAdmin = { nom: '', email: '', password: '' };


  newOffre = {
    title: '',
    contractype: '',
    description: '',
    company: '',
    location: '',
    salary: null,
    source: ''
  };

  searchEmail = '';
  searchOffreTitre=''
  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.getAdmins();
    this.getCandidats();
    this.getOffres();
  }

  getAdmins() {
    this.adminService.getAdmins().subscribe(data => this.admins = data);
  }

  ajouterAdmin() {
    this.adminService.addAdmin(this.newAdmin).subscribe(() => {
      this.getAdmins();
      this.newAdmin = { nom: '', email: '', password: '' };
    });
  }

  getCandidats() {
    this.adminService.getCandidats().subscribe(data => this.candidats = data);
  }

  getOffres() {
    this.adminService.getOffres().subscribe(data => this.offres = data);
  }

  ajouterOffre() {
    this.adminService.addOffre(this.newOffre).subscribe(() => {
      this.getOffres();

      // Réinitialise tous les champs du formulaire
      this.newOffre = {
        title: '',
        contractype: '',
        description: '',
        company: '',
        location: '',
        salary: null,
        source: ''
      };
    });
  }

  deleteOffre(id: number) {
    this.adminService.deleteOffre(id).subscribe(() => this.getOffres());
  }

}
