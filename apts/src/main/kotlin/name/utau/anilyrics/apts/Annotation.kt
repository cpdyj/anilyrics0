package name.utau.anilyrics.apts

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.metadata.deserialization.Flags
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import javax.annotation.processing.Processor
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@Target(AnnotationTarget.CLASS)
annotation class XxOf

@AutoService(Processor::class)
class XxOfProcessor : AbstractProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        roundEnv?.processingOver()?.let { if (it) return true }

        roundEnv?.rootElements?.filter { it.kind == ElementKind.CLASS }?.forEach(this::parseElement)
        return true
    }

    private fun parseElement(it: Element) {
        val xxOf = it.getAnnotation(XxOf::class.java) ?: return
        val metadata = it.getAnnotation(Metadata::class.java) ?: return
        val (nameResolver, classProto) = JvmProtoBufUtil.readClassDataFrom(metadata.data1, metadata.data2)
        val flags = classProto.flags
        val isData = Flags.IS_DATA.get(flags)
        if (!isData) return
        classProto.constructorOrBuilderList.first().valueParameterList.map {
            val name=nameResolver.getString(it.name)
            val fullName=nameResolver.getString(it.varargElementTypeId)
            val d=nameResolver.getString(it.defaultInstanceForType.typeId)

            name to d
        }.let { println(it) }

    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf("name.utau.anilyrics.apts.XxOf")
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun init(processingEnv: ProcessingEnvironment?) {
        println("processingEnv: $processingEnv")
    }
}

data class CCCC(val c:Int=500)