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
import { authGuard } from './_guard/auth.guard';
import { NotPermissionComponent } from './home/not-permission/not-permission.component';
import { authUserGuard } from './_guard/auth-user.guard';
import { authAdminGuard } from './_guard/auth-admin.guard';
import { authModeratorGuard } from './_guard/auth-moderator.guard';

const routes: Routes = [
  { path: 'home', component: HomeComponent, canActivate: [authGuard], children: [
    {path: 'not-permission', component: NotPermissionComponent}
  ] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent, canActivate: [authAdminGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'user-board', component: BoardUserComponent, canActivate: [authUserGuard] ,  children: [
    { path: 'leads', component: LeadsComponent, canActivate: [authUserGuard]},
    { path: 'add-lead', component: AddLeadComponent, canActivate: [authUserGuard] },
    { path: 'clients', component: ClientsComponent, canActivate: [authUserGuard]},
    { path: 'client-workplace/:id', component: ClientWorkplaceComponent, canActivate: [authUserGuard]}
  ]},
  { path: 'mod', component: BoardModeratorComponent, canActivate: [authModeratorGuard] },
  { path: 'admin', component: BoardAdminComponent, canActivate: [authAdminGuard]  },
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