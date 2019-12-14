package com.maklumi

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import ktx.assets.getResolver

object Utility {
    private val TAG = javaClass.simpleName
    lateinit var assetManager: AssetManager

    fun loadMapAsset(path: String) {
        val filePathResolver = Files.FileType.Internal.getResolver()
        if (filePathResolver.resolve(path).exists()) {
            assetManager.setLoader(TiledMap::class.java, TmxMapLoader(filePathResolver))
            assetManager.load(path, TiledMap::class.java)
            assetManager.finishLoadingAsset<TiledMap>(path)
        } else {
            Gdx.app.debug(TAG, "Map doesn't exist!: $path")
        }
    }

    fun getMapAsset(path: String): TiledMap? = assetManager.get(path, TiledMap::class.java)

}