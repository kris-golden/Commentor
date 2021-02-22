package com.goldenworx.commentor

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

/* Overview
Challenge: provide an architecture to save a Comment class, and other future classes.  The solution
 must leverage code reuse and the ability to change the Remote Store without affecting the existing
 Comment class.

High Level Solution: Provide an abstraction layer for the storage mechanism and use inheritance
 for increased code re-use abstraction.  SOLID principles were used where appropriate.
 */



/* Code Reuse
Challenge: Use a pattern to leverage shared code for storage.  DRY principle.
Challenge: Avoid future changes to Comment if storage methodology changes.

Solution: Create a base class that is used by other classes needing 'Storage' functionality.
 Inheritance allows for code reuse and virtualization. Use of @Serializable attribute allows
 for different types of serialization as needed by different types of data stores.

Limitations: Requiring classes to be part of the same inheritance tree can be design limiting
 and prevent those classes from being placed into a more natural inheritance.  Interface
 abstraction may be superior in those situations.
*/

@Serializable
sealed class Storeable{
    abstract var id: Int
    open fun save(){
        //Potential to save a tree of objects, or provide custom serialization.
    }
}

@Serializable
class Comment(val commentText: String = "") : Storeable() {
    override var id: Int = 0
    override fun save(){}
}

@Serializable
class Annotation(override var id: Int = 0): Storeable()




/* Abstract Storage Mechanism
Challenge: Allow the remote store to be changed at some point in the future.

Solution: Inversion of Control.  In this case, a Service Locator pattern was used to provide an
 abstraction on storage type along with an IStorage interface.  This will permit dynamic changes
 in the storage methodology and, combined with using Serialization, provide a generic method to
 store the object data.  Dependency Injection could also have been used.

Limitation: In certain situations, code will be required to be aware of the Service Locator to
 access storage, whereas DI potentially would not.
*/

//Abstraction for storage types (JSON, binary, etc.).
interface IStorage{
    fun save(store: Storeable): Int
    fun<T: Storeable> load(someId: Int): T
}

//Service Locator pattern used to retrieve the 'correct' storage mechanism.
class ServiceLocator{
    companion object{
        fun getStorage(): IStorage{
            //Service lookup would occur here.
            return JsonStorageImplementation()
        }
    }
}

//Pseudo implementation of a storage abstraction using JSON.
class JsonStorageImplementation : IStorage{
    override fun save(store: Storeable): Int{
        //Serialize Storeable to json and call remote storage.
        return 0 //Potentially return an id of stored object for future updates/retrievals.
    }
    override fun<T: Storeable> load(someId: Int): T{
        //Retrieve json from storage.

        //Inflate appropriate object here.

        //Return inflated object.
        return Comment("Test") as T
    }
}

//Test code.
fun test(id: Int){
    var baseStore: Storeable = Comment("TestJson")
    val strJson = Json.encodeToString(baseStore)

    val newComment = Comment("SaveComment")
    newComment.id = ServiceLocator.getStorage().save(newComment)
    val loadedComment = ServiceLocator.getStorage().load<Comment>(newComment.id)


}