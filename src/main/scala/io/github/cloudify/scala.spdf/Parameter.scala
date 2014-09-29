package io.github.cloudify.scala.spdf

/**
 * Describes a command line option
 */
trait Parameter[T] {
  /**
   * The underlying value of this option
   */
  private var underlying: Option[T] = None

  /**
   * The commandline name for this parameter
   */
  val name: String

  /**
   * The optional default value for this parameter
   */
  val default: Option[T] = None

  /**
   * Sets a new value for this parameter
   */
  def :=(newValue: T): Unit = underlying = Some(newValue)

  /**
   * Converts this option to a sequence of strings to be appended to the
   * command line
   */
  def toParameter(implicit shower: ParamShow[T]): Iterable[String] = value match {
    case Some(v) => shower.show(name, v)
    case _ => Iterable.empty
  }

  /**
   * Provides the current value for the option
   */
  protected def value: Option[T] = underlying orElse default

}

case class NegatableParameter(name: String) extends Parameter[Boolean] {
  override def toParameter(implicit shower: ParamShow[Boolean]): Iterable[String] = value match {
    case Some(true) => shower.show(name, true)
    case Some(false) => shower.show(s"no-$name", true)
    case _ => Iterable.empty
  }
}

object Parameter {

  /**
   * Creates a new CommandOption with the specified name and default value
   */
  def apply[T : ParamShow](commandName: String, defaultValue: T): Parameter[T] =
    new Parameter[T] {
      override val name: String = commandName
      override val default: Option[T] = Some(defaultValue)
    }

  /**
   * Creates a new CommandOption with the specified name
   */
  def apply[T : ParamShow](commandName: String): Parameter[T] =
    new Parameter[T] {
      override val name: String = commandName
      override val default: Option[T] = None
    }

}
