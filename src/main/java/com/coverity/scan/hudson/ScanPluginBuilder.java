package com.coverity.scan.hudson;

import hudson.Launcher;
import hudson.Extension;
import hudson.XmlFile;
import hudson.util.FormValidation;
import org.hudsonci.maven.plugin.builder.MavenBuilder;
import org.hudsonci.maven.plugin.builder.MavenBuilderDescriptor;
//import org.hudsonci.utils.plugin.ui.RenderableEnum;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import hudson.plugins.git.GitSCM;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.JobProperty;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Cause;
import hudson.model.Descriptor.FormException;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.scm.SCMDescriptor;
import hudson.scm.SCM;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Ant;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl\#newInstance(StaplerRequest)} is invoked
 * and a new {@link ScanPluginBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link \#name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link \#perform(AbstractBuild, Launcher, BuildListener)} method
 * will be invoked.
 */
public class ScanPluginBuilder extends Builder {

    private final String name;
	private String password;
	private String email;
	private String project;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public ScanPluginBuilder(String name, String password, String email, String project) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.project = project;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getProject() {
        return project;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // this is where you 'build' the project
        // since this is a dummy, we just say 'hello world' and call that a build


		List<Cause> buildStepCause = new ArrayList();
		 buildStepCause.add(new Cause() {
		   public String getShortDescription() {
			 return "Build Step started by Coverity Scan plugin Builder";
		   }
		 });
		 buildStepCause.add(new Cause() {
			   public String getShortDescription() {
				 return "Build Step showing Build object";
			   }
			 });
		 buildStepCause.add(new Cause() {
			   public String getShortDescription() {
				 return "Build Step showing Launcher object";
			   }
			 });

        listener.started(buildStepCause);

        // this also shows how you can consult the global configuration of the builder
        if(getDescriptor().useFrench())
            listener.getLogger().println("Bonjour, "+name+" " + password + " " + email + " " + project + "!");
        else
            listener.getLogger().println("Hello, "+name+" " + password + " " + email + " " + project + "!");

        listener.finished(Result.SUCCESS);


        // now showing the build object
        listener.started(buildStepCause);
        AbstractProject<?,?> buildProj = build.getProject();
        listener.getLogger().println("This project is called: "+buildProj.getName());
        listener.getLogger().println("Its unique full name is: "+buildProj.getFullName());
        listener.getLogger().println("Its display name is: "+buildProj.getDisplayName());
        listener.getLogger().println("Its BuildStatusUrl is: "+buildProj.getBuildStatusUrl());
        listener.getLogger().println("Its AssignedLabel is: "+buildProj.getAssignedLabelString());
        listener.getLogger().println("Its CreatedBy is: "+buildProj.getCreatedBy());
        listener.getLogger().println("Its CreationTime is: "+buildProj.getCreationTime());
        listener.getLogger().println("Its next build number is: "+buildProj.getNextBuildNumber());
        listener.getLogger().println("Its current build number is: "+buildProj.getNearestOldBuild(buildProj.getNextBuildNumber()).getNumber());

        listener.getLogger().println("build object says the build number is: "+ build.number);
        listener.getLogger().println("build object says the build getNumber is: "+ build.getNumber());
        listener.getLogger().println("build object says the build getDescription is: "+ build.getDescription());
        listener.getLogger().println("build object says the build getDisplayName is: "+ build.getDisplayName());
        listener.getLogger().println("build object says the build getId is: "+ build.getId());
        listener.getLogger().println("build object says the build getHudsonVersion is: "+ build.getHudsonVersion());
        listener.getLogger().println("build object says the build getDurationString is: "+ build.getDurationString());

        XmlFile projXml = buildProj.getConfigFile();

        listener.getLogger().println("Project config file location:");
        listener.getLogger().println(projXml.toString());
        listener.finished(Result.SUCCESS);

        try {
        	//read() => XStream.fromXML(reader)
        	Object theXml=projXml.read();
        	listener.getLogger().println("Project config file:");
            //listener.getLogger().println(theXml.toString());
        	// most of the times this should be hudson.model.FreeStyleProject
            listener.getLogger().println("object type "+theXml.getClass().getName());
            if ("hudson.model.FreeStyleProject".equals(theXml.getClass().getName())) {
            	listener.getLogger().println("Rebuilding the FreeStyleProject from the Xml config file");
            	FreeStyleProject realProj = (FreeStyleProject) theXml;
            	List projBuilders = realProj.getBuilders();

            	Iterator<Builder> iteratorBuilder = projBuilders.iterator();
            	
            	int i=0;
            	while (iteratorBuilder.hasNext()) {
            		i++;
            		Builder iBuilder = iteratorBuilder.next();

            		listener.getLogger().println("builder "+ i + " : ");  // iBuilder.toString() crashes
            		listener.getLogger().println("builder "+ i + " : " + iBuilder.getClass().toString());
            		if ("org.hudsonci.maven.plugin.builder.MavenBuilder".equals(iBuilder.getClass().getName())) {

            			//MavenBuilder mvnBuilder = (MavenBuilder) iBuilder;
            			//mvnBuilder.getConfiguration().toString();
            		}
            		if ("hudson.tasks.BatchFile".equals(iBuilder.getClass().getName())) {
            			BatchFile batchBuilder = (BatchFile) iBuilder;
            			listener.getLogger().println("The Windows batch command was:" + batchBuilder.getCommand());
            		}	

            		if ("hudson.tasks.Ant".equals(iBuilder.getClass().getName())) {
            			Ant antBuilder = (Ant) iBuilder;
            			listener.getLogger().println("the Ant command was : ant " + antBuilder.getTargets());
            		}
            		if ("hudson.tasks.Shell".equals(iBuilder.getClass().getName())) {
            			Shell shellBuilder = (Shell) iBuilder;
            			listener.getLogger().println("the shell command was : " + shellBuilder.getCommand());
            		}	
            		
            		Collection<? extends Action> stepActions = iBuilder.getProjectActions(buildProj);
            		listener.getLogger().println("builder "+ i + " : got "+ stepActions.size() +" Actions" );
            		Iterator<? extends Action> iteratorActions = stepActions.iterator();
            		if (0==stepActions.size()) {
            			listener.getLogger().println("This builder "+ i + " contains no action");
            		} else {
            			listener.getLogger().println("This builder "+ i + " contains "+stepActions.size()+ " actions");
	            		listener.getLogger().println("builder "+ i + " : got iterator ");
	            		int j=0;
	            		while (iteratorActions.hasNext()) {
	            			j++;
	            			listener.getLogger().println("builder "+ i + " Action " + j);
	            			Action iAction = iteratorActions.next();
	            			listener.getLogger().println("builder "+ i + " Action " + j + " content:");
	            			listener.getLogger().println("builder "+ i + " action " + j + " : " + iAction.toString());
	            		}
            		}
            	}


            		
            } else {
            	listener.getLogger().println("This is not a FreeStyleProject");
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        listener.getLogger().println("");
        listener.getLogger().println("shorter way to get to the project (without using xml file)");

        FreeStyleProject realProj = (FreeStyleProject) build.getProject();
    	List projBuilders = realProj.getBuilders();

    	Iterator<Builder> iteratorBuilder = projBuilders.iterator();
    	
    	int i=0;
    	while (iteratorBuilder.hasNext()) {
    		i++;
    		Builder iBuilder = iteratorBuilder.next();

    		listener.getLogger().println("builder "+ i + " : ");  // iBuilder.toString() crashes
    		listener.getLogger().println("builder "+ i + " : " + iBuilder.getClass().toString());
    		if ("org.hudsonci.maven.plugin.builder.MavenBuilder".equals(iBuilder.getClass().getName())) {

    			MavenBuilder mvnBuilder = (MavenBuilder) iBuilder;
    			listener.getLogger().println("mvnBuilder.toString()" + mvnBuilder.toString());
    			MavenBuilderDescriptor mvnBuilderDesc=mvnBuilder.getDescriptor();
    			listener.getLogger().println("mvnBuilderDesc.toString()" + mvnBuilderDesc.toString());
    			listener.getLogger().println("mvnBuilderDesc.getDisplayName()" + mvnBuilderDesc.getDisplayName());
    			listener.getLogger().println("mvnBuilderDesc.getConfigFile().toString()" + mvnBuilderDesc.getConfigFile().toString());
    			
    			//RenderableEnum[] mvnMake = mvnBuilderDesc.getMakeModeValues();
    			listener.getLogger().println("The Maven command was : mvn " + mvnBuilder.getConfig().getGoals());
    			
    			
    		
    		}
    		if ("hudson.tasks.BatchFile".equals(iBuilder.getClass().getName())) {
    			BatchFile batchBuilder = (BatchFile) iBuilder;
    			listener.getLogger().println("The Windows batch command was:" + batchBuilder.getCommand());
    		}	

    		if ("hudson.tasks.Ant".equals(iBuilder.getClass().getName())) {
    			Ant antBuilder = (Ant) iBuilder;
    			listener.getLogger().println("the Ant command was : ant " + antBuilder.getTargets());
    		}
    		if ("hudson.tasks.Shell".equals(iBuilder.getClass().getName())) {
    			Shell shellBuilder = (Shell) iBuilder;
    			listener.getLogger().println("the shell command was : " + shellBuilder.getCommand());
    		}	
    		
    		Collection<? extends Action> stepActions = iBuilder.getProjectActions(buildProj);
    		listener.getLogger().println("builder "+ i + " : got "+ stepActions.size() +" Actions" );
    		Iterator<? extends Action> iteratorActions = stepActions.iterator();
    		if (0==stepActions.size()) {
    			listener.getLogger().println("This builder "+ i + " contains no action");
    		} else {
    			listener.getLogger().println("This builder "+ i + " contains "+stepActions.size()+ " actions");
        		listener.getLogger().println("builder "+ i + " : got iterator ");
        		int j=0;
        		while (iteratorActions.hasNext()) {
        			j++;
        			listener.getLogger().println("builder "+ i + " Action " + j);
        			Action iAction = iteratorActions.next();
        			listener.getLogger().println("builder "+ i + " Action " + j + " content:");
        			listener.getLogger().println("builder "+ i + " action " + j + " : " + iAction.toString());
        		}
    		}
    	}



        // further tries to extract the build command
        listener.getLogger().println("");
        listener.getLogger().println("Further tries to extract the build command");
        //build.
        SCM projSCM = buildProj.getScm();
        listener.getLogger().println("scm.toString="+projSCM.toString());
        listener.getLogger().println("scm.type="+projSCM.getType());
		 SCMDescriptor<?> scmDesc = projSCM.getDescriptor();
		 listener.getLogger().println("the SCM descriptor is: " + scmDesc.getClass().getName());
		 listener.getLogger().println("the SCM descriptorUrl is: " + scmDesc.getDescriptorUrl());
		// listener.getLogger().println("the SCM descriptorUrl is: " + scmDesc.getDescriptorUrl());
 		if ("hudson.plugins.git.GitSCM".equals(projSCM.getType())) {
	            GitSCM theSCM;
                   // try {
                    	theSCM = (GitSCM) projSCM;
                        listener.getLogger().println("the git command was : getGitConfigName " + theSCM.getGitConfigName());
                    	listener.getLogger().println("the git command was : getGitConfigEmail " + theSCM.getGitConfigEmail());
                    	listener.getLogger().println("the git command was : getGitTool " + theSCM.getGitTool());
                    	//<? extends RemoteConfig>
                        Iterator  repositoryIterator = theSCM.getRepositories().iterator();
                        //org.eclipse.jgit.transport.RemoteConfig
                        
                       // if (0==repositoryIterator.size()) {
    					//	listener.getLogger().println("This Git Scm  contains no Repositories");
    					//} else {
    					//	listener.getLogger().println("This Git Scm contains "+repositoryIterator.size()+ " Repositories");
        					int j=0;
        					while (repositoryIterator.hasNext()) {
        					j++;
        					listener.getLogger().println("Git Scm  repository " + j);
        					RemoteConfig jConfig = (RemoteConfig) repositoryIterator.next();
        					listener.getLogger().println("Git Scm  repository  " + j + " content:");
        					listener.getLogger().println("Git Scm  repository  " + j + " : " + jConfig.getName());
        					listener.getLogger().println("Git Scm  repository  " + j + " : " + jConfig.getName());
        						Iterator  URIIterator = jConfig.getURIs().iterator();
	        					int k=0;
	        					while (URIIterator.hasNext()) {
	        					k++;
	        					listener.getLogger().println("Git Scm " + j + " URI " +k);
	        					URIish kURI = (URIish) URIIterator.next();
	        					listener.getLogger().println("Git Scm  repository  " + j + " URI " +k+ " content:");
	        					listener.getLogger().println("Git Scm  repository  " + j + " URI " +k+ " toPrivateString : " + kURI.toPrivateString());
	        					listener.getLogger().println("Git Scm  repository  " + j + " URI " +k+ " getHost : " + kURI.getHost());
	        				}
        				}
    				//}
                      
                   // } catch (FormException ex) {
                    //    Logger.getLogger(ScanPluginBuilder.class.getName()).log(Level.SEVERE, null, ex);
                   // }
                    //theSCM = (GitSCM) (projSCM.getDescriptor().newInstance(null, null));
        } else {
        	listener.getLogger().println("this SCM is not GIT");
        }
			 
 		
       //launcher.
       //listener.

        // Creating link to the report
       // ScanPluginReport report = new ScanPluginReport(buildProj.getFullName(),buildProj.getNearestOldBuild(buildProj.getNextBuildNumber()).getNumber());
        ScanPluginReport report = new ScanPluginReport(buildProj.getFullName(),build.getNumber());

        build.addAction(report);
        //build.getActions().add(report);  // identical


        listener.finished(Result.SUCCESS);
        return true;
    }

    // overrided for better type safety.
    // if your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link ScanPluginBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>views/hudson/plugins/scan-plugin/ScanPluginBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // this marker indicates Hudson that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean useFrench;

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            if(value.length()==0)
                return FormValidation.error("Please set a name");
            if(value.length()<4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Coverity Scan";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         */
        public boolean useFrench() {
            return useFrench;
        }
    }
}

