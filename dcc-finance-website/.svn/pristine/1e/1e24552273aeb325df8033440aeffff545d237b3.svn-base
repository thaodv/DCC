var gulp = require('gulp'),
    browserSync = require('browser-sync').create(), // 静态服务器
    watch = require('gulp-watch'),
    clean = require('gulp-clean'),
    assetRev = require('gulp-asset-time'),
    cssmin = require('gulp-clean-css'),
    htmlmin = require('gulp-htmlmin'),
    pngquant = require('imagemin-pngquant'), // 深度压缩
    imageMin = require('gulp-imagemin'),//压缩图片
    jsmin = require('gulp-uglify'),
    zip = require('gulp-zip'),
    autoprefixer = require('gulp-autoprefixer'),
    options = {
        removeComments: true, //清除HTML注释
        collapseWhitespace: true, //压缩HTML
        collapseBooleanAttributes: true, //省略布尔属性的值 <input checked="true"/> ==> <input />
        removeEmptyAttributes: true, //删除所有空格作属性值 <input id="" /> ==> <input />
        removeScriptTypeAttributes: true, //删除<script>的type="text/javascript"
        removeStyleLinkTypeAttributes: true, //删除<style>和<link>的type="text/css"
        minifyJS: true, //压缩页面JS
        minifyCSS: true //压缩页面CSS
    };
var app={
    distPath:'../package/site'
}
//清空dist目录
gulp.task('clear-dist-dir', function() {
    return gulp.src([app.distPath])
        .pipe(clean());
});

//拷贝项目所有源文件到dist目录下，并排除node_modules, gulpfile.js, package.json, README.md这些目录和文件
gulp.task('npm', ['clear-dist-dir'], function() {
    return gulp.src('./site/**/*')
        .pipe(gulp.dest(app.distPath));
});

//拷贝项目所有源文件到dist目录下，并排除node_modules, gulpfile.js, package.json, README.md这些目录和文件
// ['clear-dist-dir']
gulp.task('copy-file-to-dist' , function() {
    return gulp.src('./site/**/*')
        .pipe(gulp.dest(app.distPath));
});

//对dist目录下的所有js文件进行压缩
// ['copy-file-to-dist']
gulp.task('js-min',['copy-file-to-dist'], function() {
    return gulp.src(['./site/**/*.js','!./site/lib/*.js'])
        .pipe(jsmin())
        .pipe(gulp.dest(app.distPath))
})


//对dist目录下的所有css文件里面的引用的（img）添加md5值
// ['js-min'],
gulp.task('css-rev',['copy-file-to-dist'], function() {
    return gulp.src(['./site/**/*.css'])
        .pipe(cssmin())
        .pipe(assetRev())
        .pipe(gulp.dest(app.distPath));
});

//对dist目录下的所有html文件里面的引用的（css, img, js）添加md5值
gulp.task('html-rev', ['css-rev'], function() {
    return gulp.src(['./site/**/*.html'])
        .pipe(htmlmin(options))
        .pipe(assetRev())
        .pipe(gulp.dest(app.distPath));
});


//gulp任务主入口
gulp.task('build', ['html-rev']);


//auto refresh pages when you modify assets

gulp.task('browser-sync', function() {
    browserSync.init({
        server: { baseDir: "./site" }
    });
});

gulp.task('watch', function() {
    gulp.watch([
        './siteInit/html/**/*.html',
        './siteInit/css/*.css',
        './siteInit/js/**/*.js',
        './index.html'
    ], browserSync.reload);
});

gulp.task('dev', ['browser-sync', 'watch']);

//compress dest file to zip
gulp.task('clean-zip', function() {
    gulp.src(['./*.zip'])
        .pipe(clean());
});

gulp.task('build-zip', ['clean-zip'], function() {
    var curTime = new Date(),
        year = curTime.getFullYear(),
        mon = curTime.getMonth() + 1,
        day = curTime.getDate(),
        hour = curTime.getHours(),
        min = curTime.getMinutes(),
        sec = curTime.getSeconds(),
        prefix = (...args) => args.map(item => item < 10 ? `0${item}` : item),
        rev = year + prefix(mon, day, hour, min, sec).join("");

    gulp.src(['./site/**'])
        .pipe(zip(rev + '.zip'))
        .pipe(gulp.dest("./"))
})

gulp.task('zip', ['build-zip'])

// 压缩图片
gulp.task('images', function () {
    gulp.src('./site/images/*/*.*')
        .pipe(imageMin({
            progressive: true,// 无损压缩JPG图片
            svgoPlugins: [{removeViewBox: false}], // 不移除svg的viewbox属性
            use: [pngquant()] // 使用pngquant插件进行深度压缩
        }))
        .pipe(gulp.dest(app.distPath+'/images'));
});


// 自动添加浏览器前缀
gulp.task('autopre', function () {
    return gulp.src('./site/css/*.*')
        .pipe(autoprefixer({
            browsers: ['last 2 versions'],
            cascade: false
        }))
        .pipe(gulp.dest(app.distPath+"/css"));
});