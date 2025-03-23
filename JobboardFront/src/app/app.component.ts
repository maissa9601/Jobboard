import { Component } from '@angular/core';
import { RouterOutlet} from '@angular/router';

import {ReactiveFormsModule} from '@angular/forms';
import {ToastrModule} from 'ngx-toastr';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    ReactiveFormsModule,ToastrModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'JobboardFront';




}
