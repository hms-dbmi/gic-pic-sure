define(["jquery", "backbone", "handlebars", "text!data/dataInfo.hbs", "text!data/dataModal.hbs", 
	"text!data/dataInfoForm.hbs" ],
    function($, BB, HBS, dataInfoTemplate, modalTemplate, dataInfoFormTemplate){

        let dataInfoView = BB.View.extend({
            initialize: function() {
                this.dataInfoTemplate = HBS.compile(dataInfoTemplate);
                this.dataInfoFormTemplate = HBS.compile(dataInfoFormTemplate);
                this.modalTemplate = HBS.compile(modalTemplate);
            },
            events: {
                "click #data-info-btn" : "showDataInfo"
            },
            showDataInfo: function(event){
            	
        		session = JSON.parse(sessionStorage.getItem("session"));
        		
        		//attache resources to session object for use in template
        		this.resources = session.resources;
        		
        		managerPrivs = sessionprivileges.filter(x => x.includes("DATA_MANAGER_"))
        		if(managerPrivs){
        			siteName = managerPrivs[0].substring("DATA_MANAGER_".length);
        		}
            	
                $("#modal-window").html(this.modalTemplate({title: "Institution Data Information"}));
                $(".modal-body").html(this.dataInfoTemplate(this));
                
                $(".close").click(function(){
                    $("#modalDialog").hide();
                });

                $("#data-info-form-btn").click(function(){
                	JSON.parse(sessionStorage.getItem("session")).privileges;
                	
                	 $(".modal-body").html(this.dataInfoFormTemplate(this));
                }.bind(this));
                
                $("#modalDialog").show();
            }
        });
        
        return {
            View: dataInfoView,
            Model: BB.Model.extend({  })
        }
    }
);