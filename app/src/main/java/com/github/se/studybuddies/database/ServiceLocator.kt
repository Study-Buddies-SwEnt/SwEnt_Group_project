package com.github.se.studybuddies.database

object ServiceLocator {
  private var dbRepository: DbRepository? = null

  fun provideDatabase(): DbRepository {
    return dbRepository ?: DatabaseConnection()
  }

  fun setMockDatabase(dbRepository: DbRepository) {
    this.dbRepository = dbRepository
  }

  fun resetDatabase() {
    dbRepository = null
  }
}
