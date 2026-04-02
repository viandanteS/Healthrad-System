import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Direttiva strutturale che mostra o RIMUOVE fisicamente un elemento dal DOM
 * in base ai ruoli dell'utente corrente.
 *
 * Uso:
 *   <button *appHasRole="['Addetto al Front-Office']">Solo per Front-Office</button>
 *   <button *appHasRole="['Medico specialista', 'Farmacista']">Per medici/farmacisti</button>
 */
@Directive({
  selector: '[appHasRole]',
  standalone: true
})
export class HasRoleDirective implements OnInit {
  @Input('appHasRole') allowedRoles: string[] = [];

  private hasView = false;

  private templateRef = inject(TemplateRef<any>);
  private viewContainer = inject(ViewContainerRef);
  private authService = inject(AuthService);

  ngOnInit() {
    const userHasRole = this.authService.hasRole(...this.allowedRoles);

    if (userHasRole && !this.hasView) {
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.hasView = true;
    } else if (!userHasRole && this.hasView) {
      this.viewContainer.clear();
      this.hasView = false;
    }
  }
}
