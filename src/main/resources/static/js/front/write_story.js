$(function() {
    initUeditor();
    niceValidator();
});

function initUeditor() {
    window.UEDITOR_HOME_URL = contextPath+"plugins/ueditor/";
    //实例化编辑器
    //建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
    var ue = UE.getEditor('editor');
    UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
    UE.Editor.prototype.getActionUrl = function(action) {
        if (action == 'uploadimage' || action == 'uploadscrawl' ) {
            return contextPath+'ueditor/uploadfile';
        } else {
            return this._bkGetActionUrl.call(this, action);
        }
    }
}
function niceValidator() {
    $("#storyForm").validator({
        rules: {
            title: [/^([\u4E00-\u9FA5]|\w){2,20}$/, "昵称应为2到20位字符"]
        },
        fields: {
            'otherId': 'required;length(3~16);remote['+contextPath+'checkid, otherId]',
            'ephoto': 'required;accept[png|jpg|bmp|gif|jpeg]',
            'title':'required;title',
            'test-editormd-markdown-doc': 'required'
        },
        valid:  function(form){
            $("#tcontent").val(UE.getEditor('editor').getContentTxt().substring(0,30)+"...");
            $("#essays").val(UE.getEditor('editor').getContent());
            form.submit();
        },
        theme:'bootstrap',
        timely:2,
        stopOnError:true
    });
}