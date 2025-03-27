const base = {
    get() {
        return {
            url : "http://localhost:8080/xschengjifenxiruoxiangfz/",
            name: "xschengjifenxiruoxiangfz",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/xschengjifenxiruoxiangfz/front/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "学生成绩分析和弱项辅助系统"
        } 
    }
}
export default base
