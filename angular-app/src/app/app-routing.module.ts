import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { BoardModeratorComponent } from './board-moderator/board-moderator.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { ClientsComponent } from './board-user/clients/clients.component';
import { AddLeadComponent } from './board-user/add-lead/add-lead.component';
import { LeadsComponent } from './board-user/leads/leads.component';
import { ClientWorkplaceComponent } from './board-user/client-workplace/client-workplace.component';
import { SideBarComponent } from './side-bar/side-bar.component';
import { MessageDialogComponent } from './side-bar/message-dialog/message-dialog.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'user-board', component: BoardUserComponent, children: [
    { path: 'leads', component: LeadsComponent},
    { path: 'add-lead', component: AddLeadComponent },
    { path: 'clients', component: ClientsComponent},
    { path: 'client-workplace/:id', component: ClientWorkplaceComponent}
  ]},
  { path: 'mod', component: BoardModeratorComponent },
  { path: 'admin', component: BoardAdminComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'side-bar', component: SideBarComponent, children: [
    { path: 'message-dialog', component: MessageDialogComponent}
  ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }