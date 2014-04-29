package name.neuhalfen.myscala.domain.infrastructure

class DummyTransaction extends Transaction {
  def beginTransaction(): Unit = {}

  @throws(classOf[TransactionRollbackException])
  def commit(): Unit = {}

  /**
   * rollback of the TX in commit.
   */
  def rollback(): Unit = {}

}
