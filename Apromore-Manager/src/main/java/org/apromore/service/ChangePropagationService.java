package org.apromore.service;

import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionChangePropagation;
import org.apromore.helper.Version;
import org.apromore.model.ProcessVersionIdType;

import java.util.List;

/**
 * Created by fengz2 on 1/10/2014.
 */
public interface ChangePropagationService {


    //*Compare two process variants, create a new merged model and save the result *//
    /**@param username username
     * @param originalVariantVersion the original version of process variant
     * @param changedVariantVersion the evolved version of process variant
     *
     *
     * @return the new merged process summary of the newly merged process**/

    List<ProcessModelVersion> changePropagationFromGtoCG(String username, ProcessVersionIdType originalVariantVersion,
                                                         ProcessVersionIdType changedVariantVersion, String newBranchName,
                                                         Version newVersionForMergedModel, NativeType natType) throws ExceptionChangePropagation;






}
