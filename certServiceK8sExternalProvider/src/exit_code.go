package app

type ExitCode struct {
	Code int
	Message string
}

var (
	FAILED_TO_CREATE_CONTROLLER_MANAGER = ExitCode{1, "unable to create k8s controller manager"}
	FAILED_TO_REGISTER_CMPv2_ISSUER_CONTROLLER = ExitCode{2, "unable to register CMPv2Issuer controller"}
	FAILED_TO_REGISTER_CERT_REQUEST_CONTROLLER = ExitCode{3, "unable to register CertificateRequestController"}
	EXCEPTION_WHILE_RUNNING_CONTROLLER_MANAGER = ExitCode{4, "an exception occurs while running k8s controller manager"}
)
