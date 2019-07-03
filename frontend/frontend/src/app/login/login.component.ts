import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { AppService } from './../app.service'
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  private email: string;
  private password: string;


  constructor(public appService: AppService, public router: Router){
  }

  ngOnInit() {
    this.email = "";
    this.password = "";
  }

  onGetEmail(event: any){
    this.email = event.target.value;
  }

  onGetPassword(event: any){
    this.password = event.target.value
  }

  makeLogin(){
    this.appService.login(this.email, this.password)
      .subscribe(resp=>{
        console.log('sucesso');
        console.log(resp);
        //this.loginEvent.emit(true);
        this.router.navigate(['/listusers']);
      },(err)=>{
        console.error(err);
        alert("Usuário/Senha inválido(s)!");
      });
  }

}
