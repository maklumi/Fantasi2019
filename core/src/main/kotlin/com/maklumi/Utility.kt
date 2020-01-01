package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin

object Utility {
    private val TAG = javaClass.simpleName
    lateinit var assetManager: AssetManager
    private val filePathResolver = InternalFileHandleResolver()

    private const val STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.atlas"
    private const val STATUSUI_SKIN_PATH = "skins/statusui.json"
    val STATUSUI_TEXTUREATLAS = TextureAtlas(STATUSUI_TEXTURE_ATLAS_PATH)
    val STATUSUI_SKIN = Skin(Gdx.files.internal(STATUSUI_SKIN_PATH), STATUSUI_TEXTUREATLAS)
    private const val ITEMS_TEXTURE_ATLAS_PATH = "skins/items.atlas"
    val ITEMS_TEXTUREATLAS = TextureAtlas(ITEMS_TEXTURE_ATLAS_PATH)

    fun loadMapAsset(path: String) {
        if (assetManager.isLoaded(path)) return
        if (filePathResolver.resolve(path).exists()) {
            assetManager.setLoader(TiledMap::class.java, TmxMapLoader(filePathResolver))
            assetManager.load(path, TiledMap::class.java)
            assetManager.finishLoadingAsset<TiledMap>(path)
        } else {
            Gdx.app.debug(TAG, "Map doesn't exist!: $path")
        }
    }

    fun getMapAsset(path: String): TiledMap? = assetManager.get(path, TiledMap::class.java)

    fun loadTextureAsset(path: String) {
        if (assetManager.isLoaded(path)) return
        if (filePathResolver.resolve(path).exists()) {
            assetManager.setLoader(Texture::class.java, TextureLoader(filePathResolver))
            assetManager.load(path, Texture::class.java)
            assetManager.finishLoadingAsset(path)
        } else {
            Gdx.app.debug(TAG, "Texture doesn't exist!: $path")
        }
    }

    fun getTextureAsset(path: String): Texture? = assetManager.get(path, Texture::class.java)

}