package com.by122006.asm

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.by122006.asm.Scanners.CommomScanner
import com.by122006.asm.Scanners.OverAllScanner
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

public class ASM_SmartRunPluginImp extends Transform implements Plugin<Project> {
    void apply(Project project) {
        /*project.task('testTask') << {
             println "Hello gradle plugin!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
         }
         //task耗时监听
         project.gradle.addListener(new TimeListener())
    */


        println "================SmartRun插件加载成功！=========="
        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(this)
    }


    @Override
    public String getName() {
        return "ASM_SmartRunPluginImp";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        System.out.println("===============ASMSmartRunPluginImp visit start===============");
        long startTime = System.currentTimeMillis()
        //遍历inputs里的TransformInput
//        println isIncremental
        inputs.each { TransformInput input -> println input.toString() }
        inputs.each { TransformInput input ->
            //遍历input里边的DirectoryInput
            input.directoryInputs.each {
                    //                DirectoryInput directoryInput ->
//
////                    println '//PluginImp find file:' +directoryInput.file.name
//                    //是否是目录
//                    if (directoryInput.file.isDirectory()) {
//                        //遍历目录
//                        directoryInput.file.eachFileRecurse {
//                            File file ->
//                                def filename = file.name;
//                                def name = file.name
//                                //这里进行我们的处理 TODO
//                                if (name.endsWith(".class") && !name.startsWith("R\$") &&
//                                        !"R.class".equals(name) && !"BuildConfig.class".equals(name)&& !name.contains("R\$SmartRun_")) {
//                                    ClassReader classReader = new ClassReader(file.bytes)
//                                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)
//                                    def className = name.split(".class")[0]
//                                    ClassVisitor cv = new SmartRunClassVisitor(className,classWriter);
//                                    cv.setFile(file)
//                                    cv.setPackageClassName(classReader.getClassName())
//                                    classReader.accept(cv, EXPAND_FRAMES)
//
//                                    byte[] code = classWriter.toByteArray()
//                                    FileOutputStream fos = new FileOutputStream(
//                                            file.parentFile.absolutePath + File.separator + name)
//                                    fos.write(code)
//                                    fos.close()
//
//                                }
////                                println '//PluginImp find file:' + file.getAbsolutePath()
//                        }
//                    }

                DirectoryInput directoryInput ->
                    System.out.println("************ OverAllScanner start **********")
                    new OverAllScanner().scan(directoryInput)
                    System.out.println("************ CommomScanner start **********")
                    new CommomScanner().scan()
                    //处理完输入文件之后，要把输出给下一个任务
                    def dest = outputProvider.getContentLocation(directoryInput.name,
                            directoryInput.contentTypes, directoryInput.scopes,
                            Format.DIRECTORY)
                    FileUtils.copyDirectory(directoryInput.file, dest)
            }

            println '===============jarInputs==============='
            input.jarInputs.each { JarInput jarInput ->
                /**
                 * 重名名输出文件,因为可能同名,会覆盖
                 */
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
//                println '//PluginImpl find Jar:' + jarInput.getFile().getAbsolutePath()

                //处理jar进行字节码注入处理 TODO

                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                FileUtils.copyFile(jarInput.file, dest)
            }

        }
        long time = startTime - System.currentTimeMillis();
        println '//===============ASMSmartRunPluginImp visit end== ' + time + 'ms' +
                ' =======//'

    }
}