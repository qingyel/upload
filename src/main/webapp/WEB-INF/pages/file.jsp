<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <meta charset="utf-8">
    <title></title>
    <!--引入CSS-->
    <link rel="stylesheet" type="text/css" href="vendors/webuploader-0.1.5/webuploader.css">
    <!--引入JS-->
    <link href="${pageContext.request.contextPath}/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/vendors/webuploader-0.1.5/webuploader.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/vendors/pnotify/dist/pnotify.custom.min.css" rel="stylesheet" />

    <script src="${pageContext.request.contextPath}/vendors/jquery/dist/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/vendors/webuploader-0.1.5/webuploader.min.js"></script>

    <script type="text/javascript">
        $(function() {
            //开始上传按钮
            // var $btn = $('#ctlBtn');
            var flie_count = 0;
            //文件信息显示区域
            var $list = $('#fileList');
            //当前状态
            // var state = 'pending';

            //初始化Web Uploader
            var uploader = WebUploader.create({
                //设置选完文件后是否自动上传
                auto: false,
                // swf文件路径
                swf: 'vendors/webuploader-0.1.5/Uploader.swf',
                // 文件接收服务端。
                // server: '/uploadFile',
                // server: '/BigFileUp',
                server: '/uploadSlice',
                // 选择文件的按钮。可选。
                // 内部根据当前运行是创建，可能是input元素，也可能是flash.
                pick: '#picker',
                chunked: true, //开启分块上传
                chunkSize: 10 * 1024 * 1024,
                chunkRetry: 3,//网络问题上传失败后重试次数
                threads: 1, //上传并发数
                fileSizeLimit: 2000 * 1024 * 1024,//最大2GB
                fileSingleSizeLimit: 2000 * 1024 * 1024,
                resize: false//不压缩
                //选择文件类型
                //accept: {
                //    title: 'Video',
                //    extensions: 'mp4,avi',
                //    mimeTypes: 'video/*'
                //}
            });

            // 当有文件被添加进队列的时候（选择文件后调用）
            uploader.on( 'fileQueued', function( file ) {
                $list.append( '<div id="' + file.id + '" class="item">' +
                    '<h4 class="info">' + file.name + '</h4>' +
                    '<p class="state">正在计算文件MD5...请等待计算完毕后再点击上传！</p>' +
                    '</div>' );

                uploader.options.formData.guid = WebUploader.guid();//每个文件都附带一个guid，以在服务端确定哪些文件块本来是一个
                // uploader.options.formData.append( opts.fileVal, blob.getSource(),encodeURI(opts.filename || owner._formData.name || ''));//源码line 6784
                console.info("guid= "+WebUploader.guid());

                //删除要上传的文件
                //每次添加文件都给btn-delete绑定删除方法
                $(".btn-delete").click(function () {
                    //console.log($(this).attr("fileId"));//拿到文件id
                    uploader.removeFile(uploader.getFile($(this).attr("fileId"), true));
                    $(this).parent().parent().fadeOut();//视觉上消失了
                    $(this).parent().parent().remove();//DOM上删除了
                });

                uploader.md5File(file)
                    .progress(function(percentage) {
                        console.log('Percentage:', percentage);
                    })
                    // 完成
                    .then(function (fileMd5) { // 完成
                        var end = +new Date();
                        console.log("before-send-file  preupload: file.size="+file.size+" file.md5="+fileMd5);
                        file.wholeMd5 = fileMd5;//获取到了md5
                        //uploader.options.formData.md5value = file.wholeMd5;//每个文件都附带一个md5，便于实现秒传

                        $('#' + file.id).find('p.state').text('MD5计算完毕，可以点击上传了');
                        console.info("MD5="+fileMd5);
                    });
            });

            //发送前填充数据
            uploader.on( 'uploadBeforeSend', function( block, data ) {
                // block为分块数据。

                // file为分块对应的file对象。
                var file = block.file;
                var fileMd5 = file.wholeMd5;
                // 修改data可以控制发送哪些携带数据。

                console.info("fileName= "+file.name+" fileMd5= "+fileMd5+" fileId= "+file.id);
                console.info("input file= "+ flie_count);
                // 将存在file对象中的md5数据携带发送过去。
                data.md5value = fileMd5;//md5
                data.fileName_ = $("#s_"+file.id).val();
                console.log("fileName_: "+data.fileName_);
                // 删除其他数据
                // delete data.key;
                if(block.chunks>1){ //文件大于chunksize 分片上传
                    data.isChunked = true;
                    console.info("data.isChunked= "+data.isChunked);
                }else{
                    data.isChunked = false;
                    console.info("data.isChunked="+data.isChunked);
                }
            });

            uploader.on('error', function(handler) {
                if(handler=="Q_EXCEED_NUM_LIMIT"){
                    console.info("超出最大文件数");
                }
                if(handler=="F_DUPLICATE"){
                    console.info("文件重复");
                }
            });


            uploader.on( 'uploadProgress', function( file, percentage ) {
                var $li = $( '#'+file.id ),
                    $percent = $li.find('.progress .progress-bar');

                // 避免重复创建
                if ( !$percent.length ) {
                    $percent = $('<div class="progress progress-striped active">' +
                        '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                        '</div>' +
                        '</div>').appendTo( $li ).find('.progress-bar');
                }

                $li.find('p.state').text('上传中');

                $percent.css( 'width', percentage * 100 + '%' );
            });


            // 文件上传成功后会调用
            uploader.on('uploadSuccess', function (file,response) {
                $('#' + file.id).find('p.state').text('已上传');
                $('#' + file.id).find(".progress").find(".progress-bar").attr("class", "progress-bar progress-bar-success");
                $('#' + file.id).find(".info").find('.btn').fadeOut('slow');//上传完后删除"删除"按钮
                $('#StopBtn').fadeOut('slow');
            });

            // 文件上传失败后会调用
            uploader.on('uploadError', function (file,reason) {
                $('#' + file.id).find('p.state').text('上传出错');
                //上传出错后进度条变红
                $('#' + file.id).find(".progress").find(".progress-bar").attr("class", "progress-bar progress-bar-danger");
                //添加重试按钮
                //为了防止重复添加重试按钮，做一个判断
                //var retrybutton = $('#' + file.id).find(".btn-retry");
                //$('#' + file.id)
                if ($('#' + file.id).find(".btn-retry").length < 1) {
                    var btn = $('<button type="button" fileid="' + file.id + '" class="btn btn-success btn-retry"><span class="glyphicon glyphicon-refresh"></span></button>');
                    $('#' + file.id).find(".info").append(btn);//.find(".btn-danger")
                }
                $(".btn-retry").click(function () {
                    //console.log($(this).attr("fileId"));//拿到文件id
                    uploader.retry(uploader.getFile($(this).attr("fileId")));
                });
            });

            // 文件上传完毕后会调用（不管成功还是失败）
            uploader.on('uploadComplete', function (file) {//上传完成后回调
                $('#' + file.id).find('.progress').fadeOut();//上传完删除进度条
                $('#' + file.id + 'btn').fadeOut('slow')//上传完后删除"删除"按钮
            });

            $("#UploadBtn").click(function () {
                uploader.upload();//上传
            });
            $("#StopBtn").click(function () {
                console.log($('#StopBtn').attr("status"));
                var status = $('#StopBtn').attr("status");
                if (status == "suspend") {
                    console.log("当前按钮是暂停，即将变为继续");
                    $("#StopBtn").html("继续上传");
                    $("#StopBtn").attr("status", "continuous");
                    console.log("当前所有文件==="+uploader.getFiles());
                    console.log("=============暂停上传==============");
                    uploader.stop(true);
                    console.log("=============所有当前暂停的文件=============");
                    console.log(uploader.getFiles("interrupt"));
                } else {
                    console.log("当前按钮是继续，即将变为暂停");
                    $("#StopBtn").html("暂停上传");
                    $("#StopBtn").attr("status", "suspend");
                    console.log("===============所有当前暂停的文件==============");
                    console.log(uploader.getFiles("interrupt"));
                    uploader.upload(uploader.getFiles("interrupt"));
                }
            });
            uploader.on('uploadAccept', function (file, response) {
                if (response._raw === '{"error":true}') {
                    return false;
                }
            });



            // // all事件（所有的事件触发都会响应到）
            // uploader.on( 'all', function( type ) {
            //     if ( type === 'startUpload' ) {
            //         state = 'uploading';
            //     } else if ( type === 'stopUpload' ) {
            //         state = 'paused';
            //     } else if ( type === 'uploadFinished' ) {
            //         state = 'done';
            //     }
            //
            //     if ( state === 'uploading' ) {
            //         $btn.text('暂停上传');
            //     } else {
            //         $btn.text('开始上传');
            //     }
            // });
            //
            // // 开始上传按钮点击事件响应
            // $btn.on( 'click', function() {
            //     if ( state === 'uploading' ) {
            //         uploader.stop();
            //     } else {
            //         uploader.upload();
            //     }
            // });
        });
    </script>
    <style>
        #picker {
            display: inline-block;
        }
        #ctlBtn {
            position: relative;
            display: inline-block;
            cursor: pointer;
            background: #EFEFEF;
            padding: 10px 15px;
            color: #2E2E2E;
            text-align: center;
            border-radius: 3px;
            overflow: hidden;
        }
        #ctlBtn:hover {
            background: #DDDDDD;
        }
    </style>
</head>
<body>
<%--<div id="uploader" class="wu-example">--%>
<%--    <div class="btns">--%>
<%--        <div id="picker">选择文件</div>--%>
<%--        <div id="ctlBtn" class="webuploader-upload">开始上传</div>--%>
<%--        <div id="StopBtn" class="webuploader-pick"--%>
<%--             style="float: left; margin-right: 10px" status="suspend">暂停上传</div>--%>
<%--    </div>--%>
<%--    <!--用来存放文件信息-->--%>
<%--    <div id="thelist" class="uploader-list"></div>--%>
<%--</div>--%>
<section class="content">
    <div class="container" style="margin-top: 20px">
        <div class="alert alert-info">可以一次上传多个大文件</div>
    </div>
    <div class="container" style="margin-top: 50px">
        <div id="uploader" class="container">
            <div class="container">
                <div id="fileList" class="uploader-list"></div>
                <!--存放文件的容器-->
            </div>
            <div class="btns container">
                <div id="picker" class="webuploader-container"
                     style="float: left; margin-right: 10px">
                    <div>
                        选择文件 <input type="file" name="file"
                                    class="webuploader-element-invisible" multiple="multiple">
                    </div>
                </div>

                <div id="UploadBtn" class="webuploader-pick"
                     style="float: left; margin-right: 10px">开始上传</div>
                <div id="StopBtn" class="webuploader-pick"
                     style="float: left; margin-right: 10px" status="suspend">暂停上传</div>
            </div>
        </div>
    </div>
</section>
</body>
</html>
