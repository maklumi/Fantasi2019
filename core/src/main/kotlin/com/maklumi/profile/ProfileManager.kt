package com.maklumi.profile


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Base64Coder
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.ObjectMap
import ktx.json.fromJson
import com.badlogic.gdx.utils.Array as gdxArray

object ProfileManager : ProfileSubject() {
    private val json = Json()
    private const val DEFAULT_PROFILE = "default"
    private const val SAVEGAME_SUFFIX = ".sav"
    var profileName: String = DEFAULT_PROFILE
    private var profiles = mutableMapOf<String, FileHandle>()
    var properties = ObjectMap<String, Any>()
    var isNewProfile = false

    init {
        initProfiles()
    }

    private fun initProfiles() {
        if (Gdx.files.isLocalStorageAvailable) {
            val files = Gdx.files.local(".").list(SAVEGAME_SUFFIX)
            files.forEach { profiles[it.nameWithoutExtension()] = it }
        }
    }

    fun writeProfileToStorage(profileName: String, fileData: String, overwrite: Boolean) {
        val fullFilename = profileName + SAVEGAME_SUFFIX
        val localFileExists = Gdx.files.local(fullFilename).exists()

        //If we cannot overwrite and the file exists, exit
        if (localFileExists && !overwrite) return

        if (Gdx.files.isLocalStorageAvailable) {
            val file: FileHandle = Gdx.files.local(fullFilename)
            file.writeString(fileData, !overwrite)
            profiles[profileName] = file
        }
/* //uncomment if encoding
        if (Gdx.files.isLocalStorageAvailable) {
            val file: FileHandle = Gdx.files.local(fullFilename)
            val encodedString = Base64Coder.encodeString(fileData)
            file.writeString(encodedString, !overwrite)
            profiles[profileName] = file
        }
        */
    }

    inline fun <reified T : Any> getProperty(key: String): T? = properties[key] as T?

    inline fun <reified T : Any> setProperty(key: String, value: T) {
        properties.put(key, value)
    }

    fun saveProfile() {
        notifyProfileObservers(ProfileEvent.SAVING_PROFILE)
        val text = json.prettyPrint(json.toJson(properties))
        writeProfileToStorage(profileName, text, true)
//        println("ProfileManager-53: $text")
    }

    fun loadProfile() {
        if (isNewProfile) {
            notifyProfileObservers(ProfileEvent.CLEAR_CURRENT_PROFILE)
            saveProfile()
        }
        val fullProfileFileName = profileName + SAVEGAME_SUFFIX
        val doesProfileFileExist = Gdx.files.local(fullProfileFileName).exists()

        if (!doesProfileFileExist) {
            println("File doesn't exist! Default created.")
            return
        }

        profiles[profileName] = Gdx.files.internal(fullProfileFileName)
/*
        val fileHandle = profiles[profileName] as FileHandle
        val s = fileHandle.readString()
        val decodedFile = Base64Coder.decodeString(s)
        properties = json.fromJson(decodedFile)
 */
        properties = json.fromJson(profiles[profileName]!!) // swap with above
        notifyProfileObservers(ProfileEvent.PROFILE_LOADED)
        isNewProfile = false
    }

    fun storeAllProfiles() {
        if (Gdx.files.isLocalStorageAvailable) {
            val paths = Gdx.files.local(".").list(SAVEGAME_SUFFIX)
            paths.forEach { profiles[it.nameWithoutExtension()] = it }
        }
    }

    fun getProfileList(): gdxArray<String> = gdxArray(profiles.keys.toTypedArray())

    fun getProfileFile(profileName: String): FileHandle? = profiles[profileName]

    fun doesProfileExist(text: String): Boolean = profiles.contains(text)

}