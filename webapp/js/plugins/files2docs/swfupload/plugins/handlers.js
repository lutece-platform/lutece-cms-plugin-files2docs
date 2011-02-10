/* Demo Note:  This demo uses a FileProgress class that handles the UI for displaying the file name and percent complete.
The FileProgress class is not part of SWFUpload.
*/


/* **********************
   Event Handlers
   These are my custom event handlers to make my
   web application behave the way I went when SWFUpload
   completes different tasks.  These aren't part of the SWFUpload
   package.  They are part of my application.  Without these none
   of the actions SWFUpload makes will show up in my application.
   ********************** */

function swfUploadPreLoad() {
	var self = this;
	var loading = function () {
		document.getElementById("divSWFUploadUI").style.display = "none";
		document.getElementById("divLoadingContent").style.display = "";

		var longLoad = function () {
			document.getElementById("divLoadingContent").style.display = "none";
			document.getElementById("divLongLoading").style.display = "";
		};
		this.customSettings.loadingTimeout = setTimeout(function () {
				longLoad.call(self)
			},
			15 * 1000
		);
	};
	
	this.customSettings.loadingTimeout = setTimeout(function () {
			loading.call(self);
		},
		1*1000
	);
}
function swfUploadLoaded() {
	var self = this;
	clearTimeout(this.customSettings.loadingTimeout);
	document.getElementById("divSWFUploadUI").style.display = "";
	document.getElementById("divLoadingContent").style.display = "none";
	document.getElementById("divLongLoading").style.display = "none";
	document.getElementById("divAlternateContent").style.display = "none";
}
   
function swfUploadLoadFailed() {
	clearTimeout(this.customSettings.loadingTimeout);
	document.getElementById("divSWFUploadUI").style.display = "none";
	document.getElementById("divLoadingContent").style.display = "none";
	document.getElementById("divLongLoading").style.display = "none";
	document.getElementById("divAlternateContent").style.display = "";
}
   
   
function fileQueued(file) {
	try {
		var progress = new FileProgress(file, this.customSettings.progressTarget);
		progress.setStatus(message_status_pending);

	} catch (ex) {
		this.debug(ex);
	}

}

function fileQueueError(file, errorCode, message) {
	try {
		if (errorCode === SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED) {
			alert( message_alert_intro + "\n" +
				( message === 0 ? message_alert_limit_reached : message_alert_select_begin + " " +
				( message > 1 ? message_alert_select_plural_first_part + " " + message + " " +
				message_alert_select_plural_second_part : message_alert_select_singular ) ) );
			return;
		}

		var progress = new FileProgress(file, this.customSettings.progressTarget);
		progress.setError();

		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			progress.setStatus(message_error_file_exceeds_size_limit);
			this.debug("Error Code: File too big, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			progress.setStatus(message_error_zero_byte_file);
			this.debug("Error Code: Zero byte file, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			progress.setStatus(message_error_invalid_filetype);
			this.debug("Error Code: Invalid File Type, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		default:
			if (file !== null) {
				progress.setStatus(message_error_unhandled_error);
			}
			this.debug("Error Code: " + errorCode + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		}
	} catch (ex) {
        this.debug(ex);
    }
}

function fileDialogComplete(numFilesSelected, numFilesQueued) {
	try {
		/* I want auto start the upload and I can do that here */
		this.startUpload();
	} catch (ex)  {
        this.debug(ex);
	}
}

function uploadStart(file) {
	try {
		/* I don't want to do any file validation or anything,  I'll just update the UI and
		return true to indicate that the upload should start.
		It's important to update the UI here because in Linux no uploadProgress events are called. The best
		we can do is say we are uploading.
		 */
		var progress = new FileProgress(file, this.customSettings.progressTarget);
		progress.setStatus(message_status_uploading);
	}
	catch (ex) {}
	
	return true;
}

function uploadProgress(file, bytesLoaded, bytesTotal) {
	try {
		var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);

		var progress = new FileProgress(file, this.customSettings.progressTarget);
		progress.setProgress(percent);
		progress.setStatus(message_status_uploading);
	} catch (ex) {
		this.debug(ex);
	}
}

function uploadSuccess(file, serverData) {
	try {
		var progress = new FileProgress(file, this.customSettings.progressTarget);
		progress.setComplete();
		progress.setStatus(message_status_complete);

	} catch (ex) {
		this.debug(ex);
	}
}

function uploadError(file, errorCode, message) {
	try {
		var progress = new FileProgress(file, this.customSettings.progressTarget);
		progress.setError();

		switch (errorCode) {
		case SWFUpload.UPLOAD_ERROR.HTTP_ERROR:
			switch(message) {
			// Item is null (501)
			case "501":
				progress.setStatus(message_error_upload_failed);
				break;
			// Invalid file name (502)
			case "502":
				progress.setStatus(message_error_invalid_filename);
				break;
			// File is duplicated (503)
			case "503":
				progress.setStatus(message_error_file_is_duplicated);
				break;
			// IO error (504)
			case "504":
				progress.setStatus(message_error_io_error);
				break;
			default:
				progress.setStatus(message_error_http_error + ": " + message);
				break;
			}
			this.debug("Error Code: HTTP Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED:
			progress.setStatus(message_error_upload_failed);
			this.debug("Error Code: Upload Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.IO_ERROR:
			progress.setStatus(message_error_io_error);
			this.debug("Error Code: IO Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.SECURITY_ERROR:
			progress.setStatus(message_error_security_error);
			this.debug("Error Code: Security Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
			progress.setStatus(message_error_upload_limit_exceeded);
			this.debug("Error Code: Upload Limit Exceeded, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_VALIDATION_FAILED:
			progress.setStatus(message_error_file_validation_failed);
			this.debug("Error Code: File Validation Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
			progress.setStatus(message_status_cancelled);
			progress.setCancelled();
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
			progress.setStatus(message_status_stopped);
			break;
		default:
			progress.setStatus(message_error_unhandled_error + ": " + errorCode);
			this.debug("Error Code: " + errorCode + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		}
	} catch (ex) {
        this.debug(ex);
    }
}

// This event comes from the Queue Plugin
function queueComplete( numFilesUploaded )
{
	// Updates the number of imported files
	var nImportedFiles = document.getElementById( "nb_imported_files" );
	nImportedFiles.value = parseInt( nImportedFiles.value ) + numFilesUploaded;
	
	if ( nImportedFiles.value > 0 )
	{
		// Updates the message status
		var strMessage = nImportedFiles.value + "&nbsp;";
		if ( nImportedFiles.value == 1 )
		{
			strMessage = strMessage + message_complete_singular;
		}
		else
		{
			strMessage = strMessage + message_complete_plural;
		}
		document.getElementById( "divStatus" ).innerHTML = strMessage;
		
		// Disables the document type code selection and the checkbox list
		document.getElementById( "document_type_code" ).disabled = true;
		if ( document.forms[0].extension )
		{
			for ( var i = 0; i < document.forms[0].extension.length; i++ )
			{
				document.forms[0].extension[i].disabled = true;
			}
		}
	}
}
