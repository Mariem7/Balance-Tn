import { Component, OnInit } from '@angular/core';
import {Title} from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'Balance-Tn';

  constructor(private titleService : Title, private router: Router, private activePage: ActivatedRoute){}

  ngOnInit(): void {
    this.checkEvents();
  }

  //to change dynamically the title of the app
  checkEvents(){
    this.router.events.subscribe( event => {
      switch (true){
        case event instanceof NavigationEnd:
          this.titleService.setTitle(this.activePage.firstChild?.snapshot.data.title);
        break;
        default:
        break;
      }
    })
  }

}
