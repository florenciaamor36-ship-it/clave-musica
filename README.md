# La Clave Música

Aplicación Android en español para buscar y escuchar audio publicado en Internet Archive, basada en MaxMediaPlayer (Apache-2.0). El catálogo consulta la API pública de Internet Archive y filtra elementos de audio con licencia Creative Commons declarada y formato MP3.

## Qué incluye esta preview

- Catálogo inicial y búsqueda por título, artista o tema.
- Reproducción en streaming con controles en segundo plano.
- Interfaz Android con Jetpack Compose y Media3.
- Sin cuenta ni clave API requerida para las consultas públicas.

## Límites importantes

No contiene el catálogo comercial de Spotify ni garantiza encontrar artistas o lanzamientos comerciales. Internet Archive incluye obras subidas por terceros: la aplicación filtra por metadatos de licencia declarados, pero el usuario debe revisar la ficha original y respetar las condiciones de cada obra. La disponibilidad puede variar por país o por cambios del servicio. No es una app probada en un teléfono hasta que se instale y reproduzca allí.

## Desarrollo

Requisitos: JDK 17, Android SDK 36 y Gradle wrapper incluido.

```sh
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Créditos y licencia

Esta versión parte de [MahabubKarim/MaxMediaPlayer](https://github.com/MahabubKarim/MaxMediaPlayer), distribuido bajo Apache License 2.0. Se conservan los avisos de copyright y la licencia original. Los cambios de esta adaptación se publican también bajo Apache-2.0. Internet Archive y los titulares de las obras conservan sus marcas y derechos respectivos.
