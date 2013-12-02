$(function() {
	$(".task button").on("click", function() {
		var that = $(this);
		var complete = that.children("span").hasClass("glyphicon-ok-sign");
		var taskid = that.data("task-id");
		$.post("/tasks/update-status", {id: taskid, done: !complete}, function() {
			that.children("span").toggleClass("glyphicon-ok-sign");
			that.children("span").toggleClass("glyphicon-ok-circle");
			that.toggleClass("btn-success");
			that.toggleClass("btn-default");
		});
	})

	$(".task-delete").on("click", function () {
		var that = $(this);
		var taskid = that.data("task-id");
		$.ajax({
			url: '/tasks/' + taskid,
			type: 'DELETE',
			success: function(result) {
				that.parents(".task").remove();
			}
		});
	});
})