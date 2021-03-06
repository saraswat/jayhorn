package soottocfg.ast.Absyn; // Java Package generated by the BNF Converter.

public class Asrt extends GuardStm {
  public final Exp exp_;
  public Asrt(Exp p1) { exp_ = p1; }

  public <R,A> R accept(soottocfg.ast.Absyn.GuardStm.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof soottocfg.ast.Absyn.Asrt) {
      soottocfg.ast.Absyn.Asrt x = (soottocfg.ast.Absyn.Asrt)o;
      return this.exp_.equals(x.exp_);
    }
    return false;
  }

  public int hashCode() {
    return this.exp_.hashCode();
  }


}
