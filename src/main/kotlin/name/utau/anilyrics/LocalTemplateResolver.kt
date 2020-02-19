package name.utau.anilyrics

import org.thymeleaf.IEngineConfiguration
import org.thymeleaf.cache.ICacheEntryValidity
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ITemplateResolver
import org.thymeleaf.templateresolver.TemplateResolution
import org.thymeleaf.templateresource.ITemplateResource
import java.io.Reader
import java.util.concurrent.ConcurrentHashMap

class LocalTemplateResolver : ITemplateResolver {

//    private val cachedVersion = ConcurrentHashMap<String, Lazy<String>>()

    override fun getName(): String = "LocalTemplateResolver"

    override fun resolveTemplate(
        configuration: IEngineConfiguration?,
        ownerTemplate: String?,
        template: String,
        templateResolutionAttributes: MutableMap<String, Any>?
    ): TemplateResolution {
        println(template)
        return TemplateResolution(
            object : ITemplateResource {
                val defaultTemplatePath = "/template/"
                val basePath =
                    template.let { if (!it.startsWith('/')) "/$it" else it }.let {
                        it.substring(0..it.lastIndexOf('/'))
                    }

                init {
                    if (template.startsWith("./") || template.startsWith("../"))
                        throw IllegalArgumentException("relative templateName is not allowed: $template")
                }


                override fun getDescription(): String =
                    "template: $template, ownerTemplate: $ownerTemplate"

                override fun reader(): Reader {
                    return javaClass.getResource("/template/$template.html").readText().reader()
                }

                override fun exists(): Boolean {
                    return true
                }

                override fun getBaseName(): String = name

                override fun relative(relativeLocation: String?): ITemplateResource {
                    TODO("Not yet implemented")
                }
            },
            TemplateMode.HTML,
            object : ICacheEntryValidity {
                override fun isCacheable(): Boolean = true

                override fun isCacheStillValid(): Boolean = true
            })
    }

    override fun getOrder(): Int = 1

}