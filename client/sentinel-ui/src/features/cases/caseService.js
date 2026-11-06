import apiClient from "@/services/apiClient";

export const caseService = {

    getCases: async (filters = {}) => {
        const response = await apiClient.get("/api/v1/cases", {
            params: filters,
        });

        return response.data;
    },

    getAllCases: async () => {
        const cases = [];
        let page = 0;
        let last = false;
        while (!last) {
            const response = await caseService.getCases({ page, size: 100 });
            cases.push(...(response.content || []));
            last = response.last ?? page + 1 >= response.totalPages;
            page += 1;
        }
        return cases;
    },

    getCaseById: async (caseId) => {
        const response = await apiClient.get(`/api/v1/cases/${caseId}`);

        return response.data;
    },

    updateStatus: async (caseId, status) => {
        const response = await apiClient.patch(
            `/api/v1/cases/${caseId}/status`,
            {status}
        );
        
        return response.data;
    },

    updatePriority: async (caseId, priority) => {
        const response = await apiClient.patch(
            `/api/v1/cases/${caseId}/priority`,
            {priority}
        );

        return response.data;
    }

};
