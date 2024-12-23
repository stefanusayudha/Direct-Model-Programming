# Prolog
I feel that market demand has slightly shifted the programming paradigm, where true modeling programming is no longer commonly practiced.

I feel that the practices and demands of software, which are mostly geared towards business needs, have made **Business Modeling** a mainstream approach and, for some reason, it has now become the backbone of most software architectures.  

Take "Clean Architecture" as an example. Clean Architecture is essentially a form of Business Modeling that prioritizes the transaction process of objects in its design.  

The question, *"When should Clean Architecture be used?"* is one I encounter very often, and the answers are often unsatisfying. Many software developers even consider Clean Architecture the best approach to building software without fully understanding the concept of modeling.  

Of course, for business-oriented software, Clean Architecture is a very reasonable concept. After all, at its core, Clean Architecture is itself a form of Business Modeling.  

However, most answers you'll find tend to highlight how Clean Architecture works rather than emphasizing the concept of **modeling** in software itself.  

Because of this, I want to revisit a fundamental concept in programming: **Modeling**. I aim to achieve this by exploring and testing true modeling methods using Kotlin.
I am creating this project to try and re-evaluate the True Modeling programming approach in the Kotlin language.

# Theory
![](https://cdn-images-1.medium.com/v2/resize:fit:1600/1*E8A88W-4MRqu7sIOJmpdwA.jpeg)
- **Modeling** is an approach to understanding, designing, and representing systems using **models**. A model is an abstract representation of reality used to explain or simulate how a system works.
- A model is an artificial object created with behavior, purpose, and characteristics.
- A model is not merely a **Record of Values**, but rather the entire mechanism, attributes, and properties.
- Theoretically - With this paradigm, you don't need to worry about: **Reloading**, **Synchronization**, **Caching**, or dealing with instances type such **singletons** or **factories**, because each object is representing realtime state and each interaction is an interaction with **true Object**.

However, I understand that in more complex systems, abstraction layers are necessary to manage dependencies, improve scalability, and maintain flexibility. In this experiment, I want to explore how True Modeling can address those challenges.

# Preview
<img src="https://miro.medium.com/v2/resize:fit:1236/format:webp/1*qslp2Jl4G-raxuc2yUNc2Q.gif" alt="An animated GIF" height="400">
All that cost only these lines of codes:

```kotlin
class User private constructor(
    override val coroutine: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val webApi: UserWebApi = UserWebApiImpl(),
) : AutomatedInstance by AutomatedInstanceImpl(
    coroutine,
    Companion::destroy,
) {
    companion object {
        private var instance: User? = null
        fun get(): User {
            return instance ?: User().also { instance = it }
        }

        private fun destroy() {
            instance = null
        }
    }

    private val _isSynchronizing = MutableStateFlow(false)
    val isSynchronizing = _isSynchronizing.automateShare(_isSynchronizing.value)

    suspend fun sync(): Result<Unit> = withContext(coroutine.coroutineContext){
        _isSynchronizing.update { true }
        runCatching {
            val json = webApi.fetchUser().map { it.jsonObject }.getOrElse { throw it }
            val name = json["name"]?.jsonPrimitive?.content.orEmpty()
            _name.update { Name(name) }
            _isSynchronizing.update { false }
        }
    }

    private val _name = MutableStateFlow(Name(""))
    val name: StateFlow<Name> = _name.automateShare(default = _name.value)

    suspend fun updateUserName(name: Name): Result<Name> = withContext(coroutine.coroutineContext){
        webApi.updateUserName(name).onSuccess { _name.update { name } }
    }

    init {
        coroutine.launch {
            // retry twice then give up
            sync().onFailure { sync() }
        }
    }
}

```
