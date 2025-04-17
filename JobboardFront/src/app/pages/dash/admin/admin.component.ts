import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../service/admin.service';
import { FormsModule } from '@angular/forms';
import {FilterByFieldPipe} from '../../../service/filter.pipe';
import { CommonModule } from '@angular/common';
import {RouterLink, RouterLinkActive} from '@angular/router';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, FilterByFieldPipe, RouterLink],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  activeTab = 'dashboard';
  active= 'admin' ;
  showAddModal=false;
  candidats: any[] = [];
  offres: any[] = [];
  admins: any[] = [];
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
  searchOffreTitre = '';


  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.getCandidats();
    this.getOffres();
    this.getAdmins();
  }

  updateCandidat(id: number) {
    this.adminService.updateCandidat(id).subscribe(() => {
      this.getCandidats();
    });
  }

  deleteCandidat(id: number) {
    this.adminService.deleteCandidat(id).subscribe(() => {
      this.getCandidats();
    });
  }
  getAdmins(){
    this.adminService.getAdmins().subscribe(data => this.admins = data);

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
