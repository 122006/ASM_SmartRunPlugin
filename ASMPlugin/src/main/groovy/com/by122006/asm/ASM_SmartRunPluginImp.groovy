package com.by122006.asm

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.by122006.asm.Scanners.CommomScanner
import com.by122006.asm.Scanners.OverAllClassVisitor
import com.by122006.asm.Scanners.OverAllScanner
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project


class ASM_SmartRunPluginImp extends Transform implements Plugin<Project> {
    void apply(Project project) {
        /*project.task('testTask') << {
             println "Hello gradle plugin!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
         }
         //task耗时监听
         project.gradle.addListener(new TimeListener())
    */


        println "================SmartRun插件加载成功！=========="

        File file=new File(project.getBuildDir().getPath()+"\\intermediates\\transforms\\ASM_SmartRunPluginImp");
        //println project.getBuildDir().getPath()+"\\intermediates\\transforms\\ASM_SmartRunPluginImp"
        file.delete();


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
        LogUtil.println("===============ASMSmartRunPluginImp visit start===============");
        long startTime = System.currentTimeMillis()
        //遍历inputs里的TransformInput
//        println isIncremental
        inputs.each { TransformInput input -> println input.toString() }
        inputs.each { TransformInput input ->
            //遍历input里边的DirectoryInput
            input.directoryInputs.each {
                DirectoryInput directoryInput ->
                    LogUtil.println("************ Scanners init **********")
                    OverAllScanner.init();
                    CommomScanner.init();
                    LogUtil.println("************ OverAllScanner start **********")
                    new OverAllScanner().scan(directoryInput)
                    LogUtil.println("************ CommomScanner start **********")
                    new CommomScanner().scan()
                    //处理完输入文件之后，要把输出给下一个任务
                    def dest = outputProvider.getContentLocation(directoryInput.name,
                            directoryInput.contentTypes, directoryInput.scopes,
                            Format.DIRECTORY)
                    FileUtils.copyDirectory(directoryInput.file, dest)
            }

            println '===============jarInputs==============='
            input.jarInputs.each { JarInput jarInput ->
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
        long time = System.currentTimeMillis() - startTime ;
        println '//===============ASMSmartRunPluginImp visit end== 用时' + time + 'ms' +
                ' =======//'

    }
}