# Build the manager binary
FROM golang:1.15.2 as builder

WORKDIR /workspace
# Copy the Go Modules manifests
COPY go.* ./

# cache deps before building and copying source so that we don't need to re-download as much
# and so that source changes don't invalidate our downloaded layer
RUN go mod download

# Copy the go source
COPY main.go main.go
COPY src/ ./src

# Build
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 GO111MODULE=on go build -a -o oom-certservice-k8s-external-provider main.go


# Runtime container definition
FROM gcr.io/distroless/static:latest
WORKDIR /
COPY --from=builder /workspace/oom-certservice-k8s-external-provider .
ENTRYPOINT ["/oom-certservice-k8s-external-provider"]
