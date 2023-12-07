import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { BoardModeratorComponent } from './board-moderator/board-moderator.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { LidsComponent } from './board-user/lids/lids.component';
import { LidWorkplaceComponent } from './board-user/lid-workplace/lid-workplace.component';
import { AddLidComponent } from './board-user/add-lid/add-lid.component';
import { AllLidsComponent } from './lids/all-lids/all-lids.component';
import { AllClientsComponent } from './lids/all-clients/all-clients.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'user', component: BoardUserComponent, children: [
    { path: 'user-lid', component: LidsComponent },
    { path: 'user-lid-workplace', component: LidWorkplaceComponent },
    { path: 'user-add-lid', component: AddLidComponent }
  ]},
  { path: 'lids', component: LidsComponent, children: [
      { path: 'all-lids', component: AllLidsComponent},
      { path: 'all-clients', component: AllClientsComponent},
  ]},
  { path: 'mod', component: BoardModeratorComponent },
  { path: 'admin', component: BoardAdminComponent },
  { path: 'board-user/user-lid', component: LidsComponent},
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }