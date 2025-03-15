import { Component } from '@angular/core';
import {ActivatedRoute, RouterOutlet} from '@angular/router';
import {AuthService} from './service/service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'JobboardFront';




}
