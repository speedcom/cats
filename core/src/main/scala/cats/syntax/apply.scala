package cats
package syntax

trait ApplySyntax1 {
  implicit def applySyntaxU[A](a: A)(implicit U: Unapply[Apply, A]): ApplyOps[U.M, U.A] =
    new ApplyOps[U.M, U.A](U.subst(a))(U.TC)
}

trait ApplySyntax extends ApplySyntax1 {
  // TODO: use simulacrum instances eventually
  implicit def applySyntax[F[_]: Apply, A](fa: F[A]): ApplyOps[F, A] =
    new ApplyOps(fa)
}

class ApplyOps[F[_], A](fa: F[A])(implicit F: Apply[F]) {
  def apply[B](f: F[A => B]): F[B] = F.apply(fa)(f)
  def apply2[B, Z](fb: F[B])(f: F[(A, B) => Z]): F[Z] = F.apply2(fa, fb)(f)
  def map2[B, Z](fb: F[B])(f: (A, B) => Z): F[Z] = F.map2(fa, fb)(f)
  def |@|[B](fb: F[B]) = new ApplyBuilder[F] |@| fa |@| fb

  /**
   * combine both contexts but only return the right value
   */
  def *>[B](fb: F[B]) = F.map2(fa, fb)((a,b) => b)

  /**
   * combine both contexts but only return the left value
   */
  def <*[B](fb: F[B]) = F.map2(fa, fb)((a,b) => a)
}
