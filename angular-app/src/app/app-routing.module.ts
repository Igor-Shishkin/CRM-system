import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { ClientsComponent } from './board-user/clients/clients.component';
import { AddLeadComponent } from './board-user/add-lead/add-lead.component';
import { LeadsComponent } from './board-user/leads/leads.component';
import { ClientWorkplaceComponent } from './board-user/client-workplace/client-workplace.component';
import { SideBarComponent } from './side-bar/side-bar.component';
import { SaveEntryDialogComponent } from './side-bar/save-entry-dialog/save-entry-dialog.component';
import { authGuard } from './_guard/auth.guard';
import { NotPermissionComponent } from './home/not-permission/not-permission.component';
import { authUserGuard } from './_guard/auth-user.guard';
import { authAdminGuard } from './_guard/auth-admin.guard';
import { OrderWorkplaceComponent } from './board-user/order-workplace/order-workplace.component';
import { RegisterNewUserComponent } from './board-admin/register-new-user/register-new-user.component';
import { UsersComponent } from './board-admin/users/users.component';
import { BlackListOfClientsComponent } from './board-user/black-list-of-clients/black-list-of-clients.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent, canActivate: [authGuard], children: [
    {path: 'not-permission', component: NotPermissionComponent}
  ] },
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'user-board', component: BoardUserComponent, canActivate: [authUserGuard] ,  children: [
    { path: 'leads', component: LeadsComponent, canActivate: [authUserGuard]},
    { path: 'add-lead', component: AddLeadComponent, canActivate: [authUserGuard] },
    { path: 'clients', component: ClientsComponent, canActivate: [authUserGuard]},
    { path: 'client-workplace/:id', component: ClientWorkplaceComponent, canActivate: [authUserGuard]},
    { path: 'order-workplace/:id', component: OrderWorkplaceComponent, canActivate: [authUserGuard]},
    { path: 'blacklist-clients', component: BlackListOfClientsComponent, canActivate: [authUserGuard]}
  ]},
  { path: 'admin', component: BoardAdminComponent, canActivate: [authAdminGuard] , children: [
    { path: 'register-new-user', component: RegisterNewUserComponent, canActivate: [authAdminGuard]}, 
    { path: 'users', component: UsersComponent, canActivate: [authAdminGuard]}
  ] },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'side-bar', component: SideBarComponent, children: [
    { path: 'message-dialog', component: SaveEntryDialogComponent}
  ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }