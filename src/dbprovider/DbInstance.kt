package dbprovider

class DbInstance {
    private var instance: DbProvider? = null
    fun getInstance() : DbProvider {
        if (instance == null) {
            instance = DbProviderImp()
        }
        return instance as DbProvider
    }
}